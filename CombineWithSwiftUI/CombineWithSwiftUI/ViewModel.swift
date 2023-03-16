import Foundation
import Combine

struct StarWarsPerson: Decodable {
    let name: String
}

struct PeopleResponse: Decodable {
    let results: [StarWarsPerson]
}

enum PeopleData {
    case initial, loading, fetching([StarWarsPerson]), loaded([StarWarsPerson]), error
}

class ViewModel: ObservableObject {
    private let getPeopleCommand = PassthroughSubject<(), Never>()
    private var cancellables = Set<AnyCancellable>()
    
    @Published var peopleData: PeopleData = .initial
    
    init() {
        setupGetPeopleSubscriber()
    }
    
    func getPeople() {
        getPeopleCommand.send()
    }
    
    private func setupGetPeopleSubscriber() {
        getPeopleCommand
            .debounce(for: .milliseconds(300), scheduler: DispatchQueue.main)
            .handleEvents(receiveOutput: { [weak self] _ in
                guard let self = self else { return }
                
                switch self.peopleData {
                case .initial, .error:
                    self.peopleData = .loading
                case .loaded(let data):
                    self.peopleData = .fetching(data)
                default:
                    ()
                }
            })
            .map {
                let url = URL(string: "https://swapi.dev/api/people?page=\(Int.random(in: 1...5))")!
                return URLSession.shared.dataTaskPublisher(for: url)
                    .tryMap{ (data, response) in
                        guard
                            let response = response as? HTTPURLResponse,
                            response.statusCode >= 200 && response.statusCode < 300 else {
                            throw URLError(.badServerResponse)
                        }
                        return data
                    }
                    .decode(type: PeopleResponse.self, decoder: JSONDecoder())
                    .catch({ error in
                        return Fail(error: error)
                            .delay(for: .seconds(1), scheduler: DispatchQueue.main)
                    })
                        .retry(3)
            }
            .switchToLatest()
            .map { $0.results }
            .receive(on: DispatchQueue.main)
            .sink(
                receiveCompletion: { [weak self] completion in
                    guard let self = self else { return }
                    
                    if case .failure = completion {
                        self.peopleData = .error
                    }
                },
                receiveValue: { [weak self] starWarsPeople in
                    guard let self = self else { return }
                    
                    self.peopleData = .loaded(starWarsPeople)
                })
            .store(in: &cancellables)
    }
}

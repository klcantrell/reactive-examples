import SwiftUI
import SwapiStore

enum PeopleDataState {
    case initial, loading, loaded([StarWarsPerson]), fetching([StarWarsPerson]), error
}

private func getPeopleDataState(_ event: PeopleData) -> PeopleDataState {
    switch event {
    case is PeopleData.Initial:
        return .initial
    case is PeopleData.Loading:
        return .loading
    case let loaded as PeopleData.Loaded:
        return .loaded(loaded.data)
    case let fetching as PeopleData.Fetching:
        return .fetching(fetching.data)
    case is PeopleData.Error:
        return .error
    default:
        fatalError("Invalid PeopleData event type")
    }
}

class ViewModel: ObservableObject {
    private let store = SwapiStore()
    private var unsubscribeFromPeople: (() -> Void)? = nil
    private var unsubscribeFromShowLoading: (() -> Void)? = nil
    
    @Published
    var peopleData: PeopleDataState = .initial
    @Published
    var showLoading: Bool = false
    
    func getPeople() {
        store.getPeople()
    }
    
    init() {
        unsubscribeFromPeople = store.subscribePeople { event in
            self.peopleData = getPeopleDataState(event)
        }
        unsubscribeFromShowLoading = store.subscribeShowLoader { event in
            self.showLoading = (event as NSNumber).boolValue
        }
    }
    
    deinit {
        unsubscribeFromPeople?()
        unsubscribeFromShowLoading?()
    }
}

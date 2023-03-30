import SwiftUI

struct ContentView: View {
    @StateObject private var viewModel = ViewModel()
    
    var text: String {
        switch viewModel.peopleData {
        case .initial:
            return "initial"
        case .loading:
            return "loading"
        case .loaded:
            return "loaded"
        case .fetching:
            return "fetching"
        case .error:
            return "error"
        }
    }
    
    var body: some View {
        NavigationView {
            ZStack {
                VStack {
                    switch viewModel.peopleData {
                    case .loaded(let people), .fetching(let people):
                        List(people, id: \.name) { person in
                            Text(person.name)
                        }
                        .scrollContentBackground(.hidden)
                        .refreshable {
                            viewModel.getPeople()
                        }
                    case .loading:
                        ProgressView()
                            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                            .offset(.init(width: 0, height: -64))
                    case .error:
                        Text("Yikes, we ran into some trouble. Try again, please")
                            .padding(.horizontal, 64)
                            .multilineTextAlignment(.center)
                            .offset(.init(width: 0, height: -64))
                            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
                    default:
                        EmptyView()
                    }
                    
                    Spacer()
                    
                    Button("Fetch them") {
                        viewModel.getPeople()
                    }
                }
                .padding(.vertical)
                
            }
            .navigationTitle("Star Wars People")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Text("Status: \(text)")
                        .font(.caption2)
                        .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .topTrailing)
                }
            }
            .frame(maxWidth: .infinity)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

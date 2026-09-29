import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        MainViewControllerKt.initialiseKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

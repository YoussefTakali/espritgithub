import { Injectable } from "@angular/core"
import { BehaviorSubject } from "rxjs"

@Injectable({
  providedIn: "root",
})
export class SidebarService {
  private sidebarVisible = new BehaviorSubject<boolean>(false)
  sidebarVisible$ = this.sidebarVisible.asObservable()

  toggleSidebar(isVisible?: boolean) {
    if (isVisible !== undefined) {
      // Set specific state
      console.log("SidebarService: Setting sidebar state to:", isVisible)
      this.sidebarVisible.next(isVisible)
    } else {
      // Toggle current state
      const newState = !this.sidebarVisible.value
      console.log("SidebarService: Toggling sidebar state to:", newState)
      this.sidebarVisible.next(newState)
    }
  }

  // Helper methods
  openSidebar() {
    console.log("SidebarService: Opening sidebar")
    this.sidebarVisible.next(true)
  }

  closeSidebar() {
    console.log("SidebarService: Closing sidebar")
    this.sidebarVisible.next(false)
  }

  get isVisible(): boolean {
    return this.sidebarVisible.value
  }
}

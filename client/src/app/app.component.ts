import { Component, type OnInit, type OnDestroy, HostListener } from "@angular/core"
import  { SidebarService } from "./services/sidebar.service"
import { Subject } from "rxjs"
import { takeUntil } from "rxjs/operators"

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>()
  isSidebarVisible = false
  isMobile = false

  constructor(private sidebarService: SidebarService) {
    this.checkIfMobile()
  }

  ngOnInit(): void {
    // Subscribe to sidebar visibility changes from service
    this.sidebarService.sidebarVisible$.pipe(takeUntil(this.destroy$)).subscribe((visible) => {
      console.log("AppComponent: Service state changed to:", visible)
      this.isSidebarVisible = visible
    })
  }

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
  }

  @HostListener("window:resize", ["$event"])
  onResize(event: any) {
    this.checkIfMobile()
  }

  // Handle navbar toggle event (if navbar emits boolean)
  onSidebarToggled(isVisible: boolean): void {
    console.log("AppComponent: Navbar toggled to:", isVisible)
    this.sidebarService.toggleSidebar(isVisible)
  }

  // Handle sidebar visibility change from sidebar component
  onSidebarVisibilityChange(isVisible: boolean): void {
    console.log("AppComponent: Sidebar component changed to:", isVisible)
    this.sidebarService.toggleSidebar(isVisible)
  }

  // Handle navbar toggle event (if navbar doesn't emit boolean)
  toggleSidebar(): void {
    console.log("AppComponent: Toggle called, current state:", this.isSidebarVisible)
    this.sidebarService.toggleSidebar(!this.isSidebarVisible)
  }

  closeSidebar(): void {
    this.sidebarService.toggleSidebar(false)
  }

  private checkIfMobile(): void {
    this.isMobile = window.innerWidth <= 737
  }
}

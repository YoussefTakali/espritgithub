import {
  Component,
  Input,
  Output,
  EventEmitter,
   OnChanges,
   OnInit,
   OnDestroy,
   SimpleChanges,
} from "@angular/core"
import {  Router, NavigationEnd } from "@angular/router"
import { filter, takeUntil } from "rxjs/operators"
import { Subject } from "rxjs"
import  { SidebarService } from "src/app/services/sidebar.service"

@Component({
  selector: "app-sidebar",
  templateUrl: "./sidebar.component.html",
  styleUrls: ["./sidebar.component.css"],
})
export class SidebarComponent implements OnChanges, OnInit, OnDestroy {
  @Input() isVisible = false
  @Output() visibilityChange = new EventEmitter<boolean>()

  private destroy$ = new Subject<void>()

  navItems = [
    {
      title: "Home",
      icon: "fa fa-home",
      route: "/home",
      isActive: false,
    },
    {
      title: "Go to Repos",
      icon: "fa fa-code-branch",
      route: "/repo-selector",
      isActive: false,
    },
  ]

  constructor(
    private router: Router,
    private sidebarService: SidebarService,
  ) {
    console.log("SidebarComponent: Constructor called, navItems:", this.navItems)
  }

  ngOnInit(): void {
    console.log("SidebarComponent: ngOnInit called")
    this.updateActiveState()

    // Subscribe to router events to update active state
    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationEnd),
        takeUntil(this.destroy$),
      )
      .subscribe(() => {
        this.updateActiveState()
      })
  }

  ngOnDestroy(): void {
    this.destroy$.next()
    this.destroy$.complete()
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes["isVisible"]) {
      console.log("SidebarComponent: isVisible changed to:", changes["isVisible"].currentValue)
    }
  }

  navigate(route: string): void {
    console.log("SidebarComponent: Navigating to:", route)

    this.router
      .navigate([route])
      .then((success) => {
        if (success) {
          console.log("Navigation successful")
          this.updateActiveState()
        } else {
          console.log("Navigation failed")
        }
      })
      .catch((error) => {
        console.error("Navigation error:", error)
      })

    // Close sidebar on mobile after navigation
    if (window.innerWidth <= 737) {
      this.closeSidebar()
    }
  }

  closeSidebar(): void {
    console.log("SidebarComponent: Close sidebar called")
    // Use the service to close
    this.sidebarService.toggleSidebar(false)
  }

  private updateActiveState(): void {
    const currentRoute = this.router.url
    console.log("SidebarComponent: Current route:", currentRoute)

    this.navItems.forEach((item) => {
      item.isActive =
        item.route === currentRoute || (item.route === "/home" && (currentRoute === "/" || currentRoute === "/home"))
    })

    console.log("SidebarComponent: Updated navItems:", this.navItems)
  }
}

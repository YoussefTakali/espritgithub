import { Component ,Input} from '@angular/core';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  @Input() isSidebarVisible: boolean = false;

  ngOnChanges() {
    console.log('Sidebar visibility in SidebarComponent:', this.isSidebarVisible);
  }
  logClick() {
    console.log('Clicked!');
  }

}

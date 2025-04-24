import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-assignmentcard',
  templateUrl: './assignmentcard.component.html',
  styleUrls: ['./assignmentcard.component.css']
})
export class AssignmentcardComponent {
  @Input() title: string = '';
  @Input() description: string = '';
  @Input() dueDate: string = '';
  @Input() course: string = '';
  @Input() submissionRate: number = 0;
  @Input() aiAssisted: boolean = false;
}

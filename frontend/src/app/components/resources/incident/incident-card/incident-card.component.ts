import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-incident-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './incident-card.component.html',
  styleUrl: './incident-card.component.css'
})
export class IncidentCardComponent {
  @Input() incident: any;
  @Output() onView = new EventEmitter<any>();
  @Output() onResolve = new EventEmitter<any>();

  view() {
    this.onView.emit(this.incident);
  }

  resolve() {
    this.onResolve.emit(this.incident);
  }
}

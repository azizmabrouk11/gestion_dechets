import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-container-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './container-card.component.html',
  styleUrl: './container-card.component.css'
})
export class ContainerCardComponent {
  @Input() container: any;
  @Output() onEdit = new EventEmitter<any>();
  @Output() onDelete = new EventEmitter<any>();

  edit() {
    this.onEdit.emit(this.container);
  }

  delete() {
    this.onDelete.emit(this.container);
  }
}

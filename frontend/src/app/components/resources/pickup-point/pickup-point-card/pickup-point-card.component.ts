import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pickup-point-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pickup-point-card.component.html',
  styleUrl: './pickup-point-card.component.css'
})
export class PickupPointCardComponent {
  @Input() point: any;
  @Output() onEdit = new EventEmitter<any>();
  @Output() onDelete = new EventEmitter<any>();

  edit() {
    this.onEdit.emit(this.point);
  }

  delete() {
    this.onDelete.emit(this.point);
  }
}

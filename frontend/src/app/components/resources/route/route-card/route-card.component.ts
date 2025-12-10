import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { RouteDetailsComponent } from '../route-details/route-details.component';

@Component({
  selector: 'app-route-card',
  standalone: true,
  imports: [CommonModule, MatDialogModule],
  templateUrl: './route-card.component.html',
  styleUrls: ['./route-card.component.css']
})
export class RouteCardComponent {
  @Input() route: any;
  @Output() onEdit = new EventEmitter<any>();
  @Output() onDelete = new EventEmitter<any>();

  constructor(private dialog: MatDialog) { }

  viewDetails() {
    this.dialog.open(RouteDetailsComponent, {
      width: '900px',
      maxWidth: '95vw',
      maxHeight: '90vh',
      data: this.route,
      panelClass: 'route-details-dialog-container'
    });
  }

  edit() {
    this.onEdit.emit(this.route);
  }

  delete() {
    this.onDelete.emit(this.route);
  }
}

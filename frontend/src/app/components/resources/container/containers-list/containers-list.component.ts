import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContainerCardComponent } from '../container-card/container-card.component';

@Component({
  selector: 'app-containers-list',
  standalone: true,
  imports: [CommonModule, ContainerCardComponent],
  templateUrl: './containers-list.component.html',
  styleUrl: './containers-list.component.css'
})
export class ContainersListComponent {
  @Input() containers: any[] = [];
  @Output() onEditContainer = new EventEmitter<any>();
  @Output() onDeleteContainer = new EventEmitter<any>();

  editContainer(container: any) {
    this.onEditContainer.emit(container);
  }

  deleteContainer(container: any) {
    this.onDeleteContainer.emit(container);
  }
}

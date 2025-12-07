import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-confirm-dialog',
  imports: [],
  templateUrl: './confirm-dialog.component.html',
  styleUrl: './confirm-dialog.component.css',
})
export class ConfirmDialogComponent {
  title: string;
  message: string;

  constructor(
    private matdialog: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string }
  ) {
    this.title = data.title || 'Confirm';
    this.message = data.message || 'Are you sure?';
  }

  onConfirm() {
    this.matdialog.close(true);
  }
  onCancel() {
    this.matdialog.close(false);
  }
}

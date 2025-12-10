import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatDialogRef } from '@angular/material/dialog';
import { OnInit } from '@angular/core';
import { Container } from '../../../../models/Container';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContainerType } from '../../../../models/enums/ContainerType';
import { MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ContainerStatus } from '../../../../models/enums/ContainerStatus';
import { PickUpPointService } from '../../../../services/pickup-point.service';
import { AppResponse } from '../../../../models/AppResponse';
import { PickUpPoint } from '../../../../models/PickUpPoint';
import { ToastService } from '../../../../services/toast.service';
@Component({
  selector: 'app-container-form',
  standalone: true,
  imports: [CommonModule,
    ReactiveFormsModule,
    MatDialogModule],
  templateUrl: './container-form.component.html',
  styleUrls: ['./container-form.component.css'],
})
export class ContainerFormComponent implements OnInit {
  isLoading: boolean = false;
  id: string = '';
  pickUpPoints: Partial<PickUpPoint>[] = [];
  containerTypes: string[] = [ContainerType.carton, ContainerType.plastique];
  containerStatus: string[] = [ContainerStatus.functionel, ContainerStatus.non_functionel];
  editMode: boolean = false;
  formGroup!: FormGroup;

  // Custom dropdown states
  showTypeDropdown: boolean = false;
  showStatusDropdown: boolean = false;
  selectedTypeName: string = '';
  selectedStatusName: string = '';
  constructor(private matDialogRef: MatDialogRef<ContainerFormComponent>, @Inject(MAT_DIALOG_DATA) public data: Partial<Container>, private formBuilder: FormBuilder, private pickUpPointService: PickUpPointService, private toastService: ToastService) { }

  ngOnInit(): void {
    this.initialise();
  }
  initialise() {
    this.editMode = !!this.data.id;
    if (this.editMode) {
      this.id = this.data.id!;
      if (this.data.containerType) {
        this.selectedTypeName = this.data.containerType === 'carton' ? ' Carton' : ' Plastique';
      }
      if (this.data.containerStatus) {
        this.selectedStatusName = this.data.containerStatus === 'functionel' ? '✅ Functionel' : '⚠️ Non-Functionel';
      }
    }
    this.formGroup = this.formBuilder.group({
      containerType: [this.data.containerType || '', Validators.required],
      capacity: [this.data.capacity || 100, [Validators.required, Validators.min(1)]],
      fillLevel: [this.data.fillLevel || 0, [Validators.required, Validators.min(0)]],
      containerStatus: [this.data.containerStatus || '', Validators.required],
    });
    // Note: pickUpPointId is managed from the Pickup Point form, not here
  }
  onSubmit() {
    if (this.formGroup.valid) {
      this.isLoading = true;
      const formData = {
        ...this.formGroup.value,
        capacity: Number(this.formGroup.value.capacity),
        fillLevel: Number(this.formGroup.value.fillLevel)
      };

      console.log('Container form submitting:', formData);
      console.log('containerStatus value:', this.formGroup.get('containerStatus')?.value);

      // Simulate async operation
      setTimeout(() => {
        this.isLoading = false;
        if (this.editMode) {
          this.matDialogRef.close({ id: this.id, ...formData });
        } else {
          this.matDialogRef.close(formData);
        }
      }, 100);
    } else {
      // Mark all fields as touched to show validation errors
      Object.keys(this.formGroup.controls).forEach(key => {
        this.formGroup.get(key)?.markAsTouched();
      });
      this.toastService.showError('Please fill in all required fields correctly');
    }
  }
  onCancel() {
    this.matDialogRef.close(null);
  }

  // Custom dropdown methods
  toggleTypeDropdown() {
    this.showTypeDropdown = !this.showTypeDropdown;
    if (this.showTypeDropdown) this.showStatusDropdown = false;
  }

  toggleStatusDropdown() {
    this.showStatusDropdown = !this.showStatusDropdown;
    if (this.showStatusDropdown) this.showTypeDropdown = false;
  }

  selectType(type: string) {
    this.selectedTypeName = type === 'carton' ? ' Carton' : ' Plastique';
    this.formGroup.patchValue({ containerType: type });
    this.showTypeDropdown = false;
  }

  selectStatus(status: string) {
    this.selectedStatusName = status === 'functionel' ? '✅ Functionel' : '⚠️ Non-Functionel';
    this.formGroup.patchValue({ containerStatus: status });
    this.showStatusDropdown = false;
  }
}

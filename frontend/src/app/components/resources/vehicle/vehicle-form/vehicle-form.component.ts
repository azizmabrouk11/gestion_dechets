import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../../services/toast.service';
import { VehiculeType } from '../../../../models/enums/VehiculeType';
import { VehiculeStatus } from '../../../../models/enums/VehiculeStatus';

@Component({
    selector: 'app-vehicle-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
    templateUrl: './vehicle-form.component.html',
    styleUrls: ['./vehicle-form.component.css']
})
export class VehicleFormComponent implements OnInit {
    formGroup!: FormGroup;
    editMode = false;
    id?: string;
    isLoading = false;

    vehicleTypes = [VehiculeType.car, VehiculeType.camion];
    vehicleStatuses = [VehiculeStatus.functional, VehiculeStatus.non_functional];

    // Custom dropdown states
    showTypeDropdown: boolean = false;
    showStatusDropdown: boolean = false;
    selectedTypeName: string = '';
    selectedStatusName: string = '';

    constructor(
        private formBuilder: FormBuilder,
        private matDialogRef: MatDialogRef<VehicleFormComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private toastService: ToastService
    ) { }

    ngOnInit(): void {
        this.initialise();
    }

    initialise() {
        this.editMode = !!this.data.id;
        if (this.editMode) {
            this.id = this.data.id!;
            if (this.data.type) {
                this.selectedTypeName = this.getVehicleTypeDisplay(this.data.type);
            }
            if (this.data.status) {
                this.selectedStatusName = this.getVehicleStatusDisplay(this.data.status);
            }
        }
        this.formGroup = this.formBuilder.group({
            matricul: [this.data.matricul || '', [Validators.required, Validators.minLength(3)]],
            type: [this.data.type || '', Validators.required],
            capacity: [this.data.capacity || 1000, [Validators.required, Validators.min(1)]],
            status: [this.data.status || 'functional', Validators.required],
            currentLocation: [this.data.currentLocation || '']
        });
        console.log('Form initialized with values:', this.formGroup.value);
        console.log('Form valid:', this.formGroup.valid);
        console.log('Form errors:', this.formGroup.errors);
    }

    onSubmit() {
        if (this.formGroup.valid) {
            this.isLoading = true;
            const formData = {
                matricul: this.formGroup.value.matricul,
                capacity: Number(this.formGroup.value.capacity),
                vehiculeType: this.formGroup.value.type,  // Map 'type' to 'vehiculeType' for backend
                vehiculeStatus: this.formGroup.value.status,  // Map 'status' to 'vehiculeStatus' for backend
                currentLocation: this.formGroup.value.currentLocation
            };

            console.log('Form is valid, submitting data:', formData);
            console.log('Type value:', this.formGroup.get('type')?.value);
            console.log('Status value:', this.formGroup.get('status')?.value);

            // Simulate async operation
            setTimeout(() => {
                this.isLoading = false;
                if (this.editMode) {
                    this.matDialogRef.close({ ...formData, id: this.id });
                } else {
                    this.matDialogRef.close(formData);
                }
            }, 100);
        } else {
            console.error('Form is invalid:', this.formGroup.errors);
            console.error('Form controls status:', {
                matricul: this.formGroup.get('matricul')?.errors,
                type: this.formGroup.get('type')?.errors,
                capacity: this.formGroup.get('capacity')?.errors,
                status: this.formGroup.get('status')?.errors
            });
            Object.keys(this.formGroup.controls).forEach(key => {
                this.formGroup.get(key)?.markAsTouched();
            });
            this.toastService.showError('Please fill in all required fields correctly');
        }
    }

    onCancel() {
        this.matDialogRef.close();
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
        console.log('selectType called with:', type);
        this.selectedTypeName = this.getVehicleTypeDisplay(type);
        this.formGroup.patchValue({ type });
        console.log('Form type value after patch:', this.formGroup.get('type')?.value);
        this.showTypeDropdown = false;
    }

    selectStatus(status: string) {
        console.log('selectStatus called with:', status);
        this.selectedStatusName = this.getVehicleStatusDisplay(status);
        this.formGroup.patchValue({ status });
        console.log('Form status value after patch:', this.formGroup.get('status')?.value);
        this.showStatusDropdown = false;
    }

    getVehicleTypeDisplay(type: string): string {
        const typeMap: { [key: string]: string } = {
            'car': ' Car',
            'camion': ' Camion'
        };
        return typeMap[type] || type;
    }

    getVehicleStatusDisplay(status: string): string {
        const statusMap: { [key: string]: string } = {
            'functional': ' Functional',
            'non_functional': ' Non-Functional'
        };
        return statusMap[status] || status;
    }
}

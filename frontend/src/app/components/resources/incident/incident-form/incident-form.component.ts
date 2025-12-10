import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../../services/toast.service';
import { PickUpPointService } from '../../../../services/pickup-point.service';
import { UserService } from '../../../../services/user.service';
import { AppResponse } from '../../../../models/AppResponse';

@Component({
    selector: 'app-incident-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
    templateUrl: './incident-form.component.html',
    styleUrl: './incident-form.component.css'
})
export class IncidentFormComponent implements OnInit {
    formGroup!: FormGroup;
    editMode = false;
    id?: string;
    isLoading = false;

    incidentStatuses = ['reported', 'in_progress', 'resolved', 'closed'];
    incidentPriorities = ['low', 'medium', 'high', 'urgent'];
    pickUpPoints: any[] = [];
    users: any[] = [];

    // Custom dropdown states
    showStatusDropdown: boolean = false;
    showPriorityDropdown: boolean = false;
    showCitizenDropdown: boolean = false;
    showPickupPointDropdown: boolean = false;
    selectedStatusName: string = '';
    selectedPriorityName: string = '';
    selectedCitizenName: string = '';
    selectedPickupPointName: string = '';
    selectedPickupPoint: any = null;

    constructor(
        private formBuilder: FormBuilder,
        private matDialogRef: MatDialogRef<IncidentFormComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private toastService: ToastService,
        private pickUpPointService: PickUpPointService,
        private userService: UserService
    ) { }

    ngOnInit(): void {
        this.initialise();
        this.loadPickUpPoints();
        this.loadUsers();
    }

    initialise() {
        this.editMode = !!this.data.id;
        if (this.editMode) {
            this.id = this.data.id!;
            if (this.data.status) {
                this.selectedStatusName = this.data.status.charAt(0).toUpperCase() + this.data.status.slice(1).replace('_', ' ');
            }
            if (this.data.priority) {
                this.selectedPriorityName = this.data.priority.charAt(0).toUpperCase() + this.data.priority.slice(1);
            }
        }
        this.formGroup = this.formBuilder.group({
            description: [this.data.description || '', [Validators.required, Validators.minLength(10)]],
            location: [this.data.location || '', Validators.required],
            status: [this.data.status || 'reported', Validators.required],
            priority: [this.data.priority || 'medium', Validators.required],
            citizenId: [this.data.citizenId || '', Validators.required],
            pickUpPointId: [this.data.pickUpPointId || ''],
            assignedTo: [this.data.assignedTo || ''],
            resolutionNotes: [this.data.resolutionNotes || '']
        });
    }

    loadPickUpPoints() {
        this.isLoading = true;
        this.pickUpPointService.getAll().subscribe({
            next: (res: AppResponse) => {
                this.pickUpPoints = res.pickuppoints || [];
                // If a pickUpPointId is already selected in the form (edit mode), set the selectedPickupPoint object
                const selectedId = this.formGroup.get('pickUpPointId')?.value;
                if (selectedId) {
                    this.selectedPickupPoint = this.pickUpPoints.find(p => p.id === selectedId) || null;
                    if (this.selectedPickupPoint) {
                        this.selectedPickupPointName = this.selectedPickupPoint.address || `Pickup Point #${this.selectedPickupPoint.id?.substring(0, 8)}`;
                    }
                }
                this.isLoading = false;
            },
            error: (err) => {
                console.error('Error fetching pickup points', err);
                this.isLoading = false;
            }
        });
    }

    loadUsers() {
        this.userService.getAll().subscribe({
            next: (res: AppResponse) => {
                this.users = (res as any).users || [];
            },
            error: (err) => {
                console.error('Error fetching users', err);
            }
        });
    }

    onSubmit() {
        if (this.formGroup.valid) {
            this.isLoading = true;
            // Simulate async operation
            setTimeout(() => {
                this.isLoading = false;
                if (this.editMode) {
                    this.matDialogRef.close({ ...this.formGroup.value, id: this.id });
                } else {
                    this.matDialogRef.close(this.formGroup.value);
                }
            }, 100);
        } else {
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
    toggleStatusDropdown() {
        this.showStatusDropdown = !this.showStatusDropdown;
        if (this.showStatusDropdown) {
            this.showPriorityDropdown = false;
            this.showCitizenDropdown = false;
            this.showPickupPointDropdown = false;
        }
    }

    togglePriorityDropdown() {
        this.showPriorityDropdown = !this.showPriorityDropdown;
        if (this.showPriorityDropdown) {
            this.showStatusDropdown = false;
            this.showCitizenDropdown = false;
            this.showPickupPointDropdown = false;
        }
    }

    toggleCitizenDropdown() {
        this.showCitizenDropdown = !this.showCitizenDropdown;
        if (this.showCitizenDropdown) {
            this.showStatusDropdown = false;
            this.showPriorityDropdown = false;
            this.showPickupPointDropdown = false;
        }
    }

    togglePickupPointDropdown() {
        this.showPickupPointDropdown = !this.showPickupPointDropdown;
        if (this.showPickupPointDropdown) {
            this.showStatusDropdown = false;
            this.showPriorityDropdown = false;
            this.showCitizenDropdown = false;
        }
    }

    selectStatus(status: string) {
        this.selectedStatusName = status.charAt(0).toUpperCase() + status.slice(1).replace('_', ' ');
        this.formGroup.patchValue({ status });
        this.showStatusDropdown = false;
    }

    selectPriority(priority: string) {
        this.selectedPriorityName = priority.charAt(0).toUpperCase() + priority.slice(1);
        this.formGroup.patchValue({ priority });
        this.showPriorityDropdown = false;
    }

    selectCitizen(user: any) {
        this.selectedCitizenName = `${user.userName} (${user.email})`;
        this.formGroup.patchValue({ citizenId: user.id });
        this.showCitizenDropdown = false;
    }

    selectPickupPoint(point: any) {
        this.selectedPickupPointName = point.address || `Pickup Point #${point.id?.substring(0, 8)}`;
        this.selectedPickupPoint = point;
        this.formGroup.patchValue({ pickUpPointId: point.id });
        this.showPickupPointDropdown = false;
    }
}

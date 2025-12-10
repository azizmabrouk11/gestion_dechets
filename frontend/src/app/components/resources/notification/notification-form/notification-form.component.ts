import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../../services/toast.service';
import { UserService } from '../../../../services/user.service';
import { ContainerService } from '../../../../services/container.service';
import { IncidentService } from '../../../../services/incident.service';
import { AppResponse } from '../../../../models/AppResponse';

@Component({
    selector: 'app-notification-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, MatDialogModule],
    templateUrl: './notification-form.component.html',
    styleUrl: './notification-form.component.css'
})
export class NotificationFormComponent implements OnInit {
    formGroup!: FormGroup;
    editMode = false;
    id?: string;
    isLoading = false;

    notificationTypes = ['incident_notification', 'capacity_notification'];
    users: any[] = [];
    containers: any[] = [];
    incidents: any[] = [];

    // Custom dropdown states
    showTypeDropdown: boolean = false;
    showUserDropdown: boolean = false;
    showContainerDropdown: boolean = false;
    showIncidentDropdown: boolean = false;
    selectedTypeName: string = '';
    selectedUserName: string = '';
    selectedContainerName: string = '';
    selectedIncidentName: string = '';

    constructor(
        private formBuilder: FormBuilder,
        private matDialogRef: MatDialogRef<NotificationFormComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any,
        private toastService: ToastService,
        private userService: UserService,
        private containerService: ContainerService,
        private incidentService: IncidentService
    ) { }

    ngOnInit(): void {
        this.initialise();
        this.loadUsers();
        this.loadContainers();
        this.loadIncidents();
    }

    initialise() {
        this.editMode = !!this.data.id;
        if (this.editMode) {
            this.id = this.data.id!;
            if (this.data.notificationType) {
                this.selectedTypeName = this.data.notificationType === 'incident_notification' ? 'Incident Notification' : 'Capacity Notification';
            }
        }
        this.formGroup = this.formBuilder.group({
            subject: [this.data.subject || '', [Validators.required, Validators.minLength(5)]],
            recipient: [this.data.recipient || '', [Validators.required, Validators.email]],
            body: [this.data.body || '', [Validators.required, Validators.minLength(10)]],
            notificationType: [this.data.notificationType || 'incident_notification', Validators.required],
            userId: [this.data.userId || ''],
            containerId: [this.data.containerId || ''],
            incidentId: [this.data.incidentId || '']
        });
    }

    loadUsers() {
        this.userService.getAll().subscribe({
            next: (res: AppResponse) => {
                this.users = (res as any).users || [];
            },
            error: (err) => console.error('Error fetching users', err)
        });
    }

    loadContainers() {
        this.containerService.getAll().subscribe({
            next: (res: AppResponse) => {
                this.containers = (res as any).containers || [];
            },
            error: (err) => console.error('Error fetching containers', err)
        });
    }

    loadIncidents() {
        this.incidentService.getAll().subscribe({
            next: (res: AppResponse) => {
                this.incidents = (res as any).incidents || [];
            },
            error: (err) => console.error('Error fetching incidents', err)
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
    toggleTypeDropdown() {
        this.showTypeDropdown = !this.showTypeDropdown;
        if (this.showTypeDropdown) {
            this.showUserDropdown = false;
            this.showContainerDropdown = false;
            this.showIncidentDropdown = false;
        }
    }

    toggleUserDropdown() {
        this.showUserDropdown = !this.showUserDropdown;
        if (this.showUserDropdown) {
            this.showTypeDropdown = false;
            this.showContainerDropdown = false;
            this.showIncidentDropdown = false;
        }
    }

    toggleContainerDropdown() {
        this.showContainerDropdown = !this.showContainerDropdown;
        if (this.showContainerDropdown) {
            this.showTypeDropdown = false;
            this.showUserDropdown = false;
            this.showIncidentDropdown = false;
        }
    }

    toggleIncidentDropdown() {
        this.showIncidentDropdown = !this.showIncidentDropdown;
        if (this.showIncidentDropdown) {
            this.showTypeDropdown = false;
            this.showUserDropdown = false;
            this.showContainerDropdown = false;
        }
    }

    selectType(type: string) {
        this.selectedTypeName = type === 'incident_notification' ? 'Incident Notification' : 'Capacity Notification';
        this.formGroup.patchValue({ notificationType: type });
        this.showTypeDropdown = false;
    }

    selectUser(user: any) {
        this.selectedUserName = user.userName;
        this.formGroup.patchValue({ userId: user.id });
        this.showUserDropdown = false;
    }

    selectContainer(container: any) {
        this.selectedContainerName = `Container #${container.id?.substring(0, 8)}`;
        this.formGroup.patchValue({ containerId: container.id });
        this.showContainerDropdown = false;
    }

    selectIncident(incident: any) {
        this.selectedIncidentName = incident.description?.substring(0, 50) + '...';
        this.formGroup.patchValue({ incidentId: incident.id });
        this.showIncidentDropdown = false;
    }
}

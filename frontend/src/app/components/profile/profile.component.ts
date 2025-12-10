import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogRef, MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserProfile } from '../../models/UserProfile';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { UserRole } from '../../models/enums/UserRole';
import { Subject, takeUntil } from 'rxjs';

interface ProfileFormData {
  userName: string;
  email: string;
  role?: string;
  driver?: boolean;
  firstName: string;
  lastName: string;
  profileImage: string | null;
}

@Component({
  selector: 'app-profile',
  imports: [MatDialogModule, ReactiveFormsModule, MatSnackBarModule, CommonModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy {
  profileForm!: FormGroup;
  selectedImage: string | ArrayBuffer | null = null;
  isLoading = false;
  imageError: string | null = null;

  private readonly destroy$ = new Subject<void>();
  private readonly MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
  private readonly ALLOWED_TYPES = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];
  private readonly DEFAULT_AVATAR = '/assets/images/userimage.png';

  constructor(
    private readonly matDialogRef: MatDialogRef<ProfileComponent>,
    @Inject(MAT_DIALOG_DATA) public readonly data: Partial<UserProfile>,
    private readonly formBuilder: FormBuilder,
    private readonly userService: UserService,
    private readonly snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.initializeForm();
    this.selectedImage = this.data.profileImage || this.DEFAULT_AVATAR;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeForm(): void {
    const isEditMode = !!this.data.userName;

    this.profileForm = this.formBuilder.group({
      userName: [{ value: this.data.userName || '', disabled: isEditMode }, [Validators.required, Validators.minLength(3)]],
      email: [{ value: this.data.email || '', disabled: isEditMode }, [Validators.required, Validators.email]],
      firstName: [
        this.data.firstName || '',
        [Validators.required, Validators.minLength(2), Validators.maxLength(50)]
      ],
      role: [
        this.data.role || '',
        [Validators.required, Validators.minLength(2), Validators.maxLength(50)]
      ],
      driver: [this.data.driver || false],
      lastName: [
        this.data.lastName || '',
        [Validators.required, Validators.minLength(2), Validators.maxLength(50)]
      ],
      profileImage: [this.data.profileImage || null]
    });
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      this.markFormGroupTouched(this.profileForm);
      this.showNotification('Please fill in all required fields correctly', 'error');
      return;
    }

    if (this.isLoading) return;

    const formValues = this.profileForm.getRawValue() as ProfileFormData;
    const updatedProfile: Partial<UserProfile> = {
      ...this.data,
      userName: formValues.userName,
      email: formValues.email,
      role: formValues.role ? (formValues.role as UserRole) : this.data.role,
      firstName: formValues.firstName.trim(),
      lastName: formValues.lastName.trim(),
      driver: formValues.driver !== undefined ? formValues.driver : this.data.driver,
      profileImage: (this.selectedImage as string) || formValues.profileImage || undefined
    };

    this.matDialogRef.close(updatedProfile);
  }

  onClose(): void {
    if (this.profileForm.dirty) {
      const confirmClose = confirm('You have unsaved changes. Are you sure you want to close?');
      if (!confirmClose) return;
    }
    this.matDialogRef.close(null);
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) return;

    this.imageError = null;

    // Validate file type
    if (!this.ALLOWED_TYPES.includes(file.type)) {
      this.imageError = 'Please select a valid image file (JPEG, PNG, or WebP)';
      this.showNotification(this.imageError, 'error');
      input.value = '';
      return;
    }

    // Validate file size
    if (file.size > this.MAX_FILE_SIZE) {
      this.imageError = 'Image size must be less than 5MB';
      this.showNotification(this.imageError, 'error');
      input.value = '';
      return;
    }

    this.loadImage(file);
  }

  private loadImage(file: File): void {
    const reader = new FileReader();

    reader.onloadstart = () => {
      this.isLoading = true;
    };

    reader.onload = (e: ProgressEvent<FileReader>) => {
      this.selectedImage = e.target?.result || null;
      this.profileForm.patchValue({ profileImage: this.selectedImage });
      this.profileForm.markAsDirty();
      this.isLoading = false;
    };

    reader.onerror = () => {
      this.imageError = 'Failed to load image. Please try again.';
      this.showNotification(this.imageError, 'error');
      this.isLoading = false;
    };

    reader.readAsDataURL(file);
  }

  removeImage(): void {
    this.selectedImage = this.DEFAULT_AVATAR;
    this.profileForm.patchValue({ profileImage: null });
    this.profileForm.markAsDirty();
    this.imageError = null;
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();
    });
  }

  private showNotification(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Close', {
      duration: 4000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: type === 'error' ? ['error-snackbar'] : ['success-snackbar']
    });
  }

  getErrorMessage(fieldName: string): string {
    const control = this.profileForm.get(fieldName);
    if (!control || !control.errors || !control.touched) return '';

    if (control.errors['required']) return `${this.getFieldLabel(fieldName)} is required`;
    if (control.errors['minlength']) {
      return `${this.getFieldLabel(fieldName)} must be at least ${control.errors['minlength'].requiredLength} characters`;
    }
    if (control.errors['maxlength']) {
      return `${this.getFieldLabel(fieldName)} must not exceed ${control.errors['maxlength'].requiredLength} characters`;
    }
    if (control.errors['email']) return 'Please enter a valid email address';

    return '';
  }

  private getFieldLabel(fieldName: string): string {
    const labels: Record<string, string> = {
      firstName: 'First name',
      lastName: 'Last name',
      email: 'Email',
      userName: 'Username',
      role: 'User role'
    };
    return labels[fieldName] || fieldName;
  }

  get isFormValid(): boolean {
    return this.profileForm.valid && !this.isLoading;
  }
}

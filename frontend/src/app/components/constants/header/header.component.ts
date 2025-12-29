import { Component, OnInit, HostListener, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppKeycloakService } from '../../../keycloak/appKeycloakService';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProfileComponent } from '../../../components/profile/profile.component';
import { UserService } from '../../../services/user.service';
import { Subscription } from 'rxjs';
import { ToastrService } from "ngx-toastr";
import { UserRole } from '../../../models/enums/UserRole';
import { NavComponent } from '../nav/nav.component';
@Component({
  selector: 'app-header',
  imports: [CommonModule, MatDialogModule, MatSnackBarModule, NavComponent],

  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  isProfileDropdownOpen = false;
  isMobileMenuOpen = false;
  profileImage!: string;
  admin = false;
  private profileSubscription?: Subscription;

  constructor(private appKeycloakService: AppKeycloakService, private matDialog: MatDialog, private userService: UserService, private toastservice: ToastrService) {

  }

  ngOnInit(): void {

    this.profileSubscription = this.appKeycloakService.profileObservable.subscribe(profile => {
      if (profile?.profileImage) {

        this.profileImage = profile.profileImage;
        this.admin = profile.role && profile?.role == UserRole.admin ? true : false;
      }
    });
  }

  ngOnDestroy(): void {
    // Clean up subscription
    if (this.profileSubscription) {
      this.profileSubscription.unsubscribe();
    }
  }

  // Use a getter to always get the current auth state
  get isAuthenitcatedUser(): boolean {
    return this.appKeycloakService.isLoggedIn();
  }

  login() {
    this.appKeycloakService.login();
  }

  signUp() {
    this.appKeycloakService.signUp();
  }

  logout() {
    this.appKeycloakService.logout();
    this.isProfileDropdownOpen = false; // Close dropdown after logout
  }

  toggleProfileDropdown() {
    this.isProfileDropdownOpen = !this.isProfileDropdownOpen;

  }





  @HostListener('document:click', ['$event'])
  closeDropdown(event: Event) {
    const target = event.target as HTMLElement;
    const button = target.closest('.profile-button');
    const dropdown = target.closest('.profile-dropdown');
    if (!button && !dropdown) {
      this.isProfileDropdownOpen = false;
    }
  }

  toggleMobileMenu() {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu() {
    this.isMobileMenuOpen = false;
  }

  openDialog() {
    const dialogRef = this.matDialog.open(ProfileComponent, {
      width: "60%",
      height: "90%",
      data: this.appKeycloakService.profile
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && result.email) {

        this.userService.updateUserByEmail(result.email, result).subscribe({
          next: response => {
            if (response.user) {
              this.appKeycloakService.updateLocalProfile(response.user);
              if (response.user.profileImage) {
                this.profileImage = response.user.profileImage;
              }
              this.toastservice.success(`${response?.message} ✅`, 'Success', {
                positionClass: 'toast-b',
                toastClass: 'toast-s',
                timeOut: 4000
              });
            }


          },
          error: (err) => {
            this.toastservice.error(`${err.error?.message} ❌`, 'Error', {
              positionClass: 'toast-b',
              toastClass: 'toast-s',
              timeOut: 4000
            });
          }
        });
      }
    });
  }
}
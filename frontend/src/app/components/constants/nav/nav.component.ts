import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { links } from '../../../models/constants';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { Subscription } from 'rxjs';
import { AppKeycloakService } from '../../../keycloak/appKeycloakService';
import { UserRole } from '../../../models/enums/UserRole';

@Component({
  selector: 'app-nav',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.css']
})
export class NavComponent implements OnInit {
  constructor(private appkeycloakSerice: AppKeycloakService) { }
  links = links;
  admin = false;
  profileSubscription!: Subscription
  ngOnInit(): void {
    this.appkeycloakSerice.profileObservable.subscribe(profile => {
      this.admin = profile?.role && profile?.role == UserRole.admin ? true : false;
    })

  }

}

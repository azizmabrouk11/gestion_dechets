import { Component, EventEmitter, input, Input, OnInit, Output } from '@angular/core';
import { UserProfile } from '../../../../models/UserProfile';
import { UserService } from '../../../../services/user.service';
import { ToastService } from '../../../../services/toast.service';
import { AppResponse } from '../../../../models/AppResponse';
import { CommonModule } from '@angular/common';
import { UserCardComponent } from '../user-card/user-card.component';
@Component({
  selector: 'app-users-list',
  imports: [CommonModule,UserCardComponent],
  templateUrl: './users-list.component.html',
  styleUrl: './users-list.component.css',
})
export class UsersListComponent implements OnInit {
 
 loading: boolean = false;
  @Input() users : any[] =[];
@Output() onEditUser = new EventEmitter<any>();
@Output() onDeleteUser = new EventEmitter<any>();
constructor(private userService: UserService, private toastService: ToastService) { }
ngOnInit(): void {


}

editUser(user: UserProfile) {
  this.onEditUser.emit(user); 
}
  deleteUser(user: UserProfile) {
    this.onDeleteUser.emit(user);
  }
    
}
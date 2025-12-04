import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ResourcesComponent } from './resources.component';
import { UserService } from '../../services/user.service';
import { ContainerService } from '../../services/container.service';
import { PickUpPointService } from '../../services/pickup-point.service';
import { of } from 'rxjs';

describe('ResourcesComponent', () => {
  let component: ResourcesComponent;
  let fixture: ComponentFixture<ResourcesComponent>;

  const mockUserService = {
    getAll: () => of({ users: [] })
  };
  const mockContainerService = {
    getAll: () => of({ containers: [] })
  };
  const mockPickUpPointService = {
    getAll: () => of({ pickuppoints: [] })
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResourcesComponent],
      providers: [
        { provide: UserService, useValue: mockUserService },
        { provide: ContainerService, useValue: mockContainerService },
        { provide: PickUpPointService, useValue: mockPickUpPointService }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ResourcesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

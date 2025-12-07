import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContainerFormComponent } from './container-form.component';

describe('ContainerFormComponent', () => {
  let component: ContainerFormComponent;
  let fixture: ComponentFixture<ContainerFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ContainerFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContainerFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

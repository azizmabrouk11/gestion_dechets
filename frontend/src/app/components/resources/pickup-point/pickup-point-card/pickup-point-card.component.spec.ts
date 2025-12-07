import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PickupPointCardComponent } from './pickup-point-card.component';

describe('PickupPointCardComponent', () => {
  let component: PickupPointCardComponent;
  let fixture: ComponentFixture<PickupPointCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PickupPointCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PickupPointCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

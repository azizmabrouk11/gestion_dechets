import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IncidentCardComponent } from './incident-card.component';

describe('IncidentCardComponent', () => {
  let component: IncidentCardComponent;
  let fixture: ComponentFixture<IncidentCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IncidentCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IncidentCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DIalogStopTicketComponent } from './dialog-stop-ticket.component';

describe('DIalogStopTicketComponent', () => {
  let component: DIalogStopTicketComponent;
  let fixture: ComponentFixture<DIalogStopTicketComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DIalogStopTicketComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DIalogStopTicketComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

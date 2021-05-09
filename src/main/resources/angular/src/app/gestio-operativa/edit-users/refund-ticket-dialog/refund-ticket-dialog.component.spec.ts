import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RefundTicketDialogComponent } from './refund-ticket-dialog.component';

describe('RefundTicketDialogComponent', () => {
  let component: RefundTicketDialogComponent;
  let fixture: ComponentFixture<RefundTicketDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RefundTicketDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RefundTicketDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

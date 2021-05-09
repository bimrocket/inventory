import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogHistoricTransactionComponent } from './dialog-historic-transaction.component';

describe('DialogHistoricTransactionComponent', () => {
  let component: DialogHistoricTransactionComponent;
  let fixture: ComponentFixture<DialogHistoricTransactionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogHistoricTransactionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogHistoricTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

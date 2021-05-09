import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DIalogSelectHolidaysComponent } from './dialog-select-holidays.component';

describe('DIalogSelectHolidaysComponent', () => {
  let component: DIalogSelectHolidaysComponent;
  let fixture: ComponentFixture<DIalogSelectHolidaysComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DIalogSelectHolidaysComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DIalogSelectHolidaysComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

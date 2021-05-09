import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MultiDateCalendarComponent } from './multi-date-calendar.component';

describe('MultiDateCalendarComponent', () => {
  let component: MultiDateCalendarComponent;
  let fixture: ComponentFixture<MultiDateCalendarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MultiDateCalendarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MultiDateCalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

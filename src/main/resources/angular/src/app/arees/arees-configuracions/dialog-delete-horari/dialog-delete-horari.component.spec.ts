import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeleteHorariComponent } from './dialog-delete-horari.component';

describe('DialogDeleteHorariComponent', () => {
  let component: DialogDeleteHorariComponent;
  let fixture: ComponentFixture<DialogDeleteHorariComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeleteHorariComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeleteHorariComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

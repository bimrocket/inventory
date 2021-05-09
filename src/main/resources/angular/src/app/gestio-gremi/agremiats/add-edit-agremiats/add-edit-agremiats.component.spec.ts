import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditAgremiatsComponent } from './add-edit-agremiats.component';

describe('AddEditAgremiatsComponent', () => {
  let component: AddEditAgremiatsComponent;
  let fixture: ComponentFixture<AddEditAgremiatsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddEditAgremiatsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddEditAgremiatsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

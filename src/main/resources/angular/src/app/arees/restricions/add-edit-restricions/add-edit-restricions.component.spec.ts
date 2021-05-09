import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditRestricionsComponent } from './add-edit-restricions.component';

describe('AddEditRestricionsComponent', () => {
  let component: AddEditRestricionsComponent;
  let fixture: ComponentFixture<AddEditRestricionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddEditRestricionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddEditRestricionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditGremiComponent } from './add-edit-gremi.component';

describe('AddEditGremiComponent', () => {
  let component: AddEditGremiComponent;
  let fixture: ComponentFixture<AddEditGremiComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddEditGremiComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddEditGremiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

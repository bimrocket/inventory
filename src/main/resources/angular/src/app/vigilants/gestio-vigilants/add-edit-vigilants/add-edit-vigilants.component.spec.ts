import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditVigilantsComponent } from './add-edit-vigilants.component';

describe('AddEditVigilantsComponent', () => {
  let component: AddEditVigilantsComponent;
  let fixture: ComponentFixture<AddEditVigilantsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddEditVigilantsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddEditVigilantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddEditVehiclesBusComponent } from './add-edit-vehicles-bus.component';

describe('AddEditVehiclesBusComponent', () => {
  let component: AddEditVehiclesBusComponent;
  let fixture: ComponentFixture<AddEditVehiclesBusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddEditVehiclesBusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddEditVehiclesBusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogEditVehicleComponent } from './dialog-edit-vehicle.component';

describe('DialogEditVehicleComponent', () => {
  let component: DialogEditVehicleComponent;
  let fixture: ComponentFixture<DialogEditVehicleComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogEditVehicleComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogEditVehicleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

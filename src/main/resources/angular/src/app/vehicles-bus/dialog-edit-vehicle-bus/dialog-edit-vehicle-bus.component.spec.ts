import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogEditVehicleBusComponent } from './dialog-edit-vehicle-bus.component';

describe('DialogEditVehicleBusComponent', () => {
  let component: DialogEditVehicleBusComponent;
  let fixture: ComponentFixture<DialogEditVehicleBusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogEditVehicleBusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogEditVehicleBusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

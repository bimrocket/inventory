import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VehiclesBusComponent } from './vehicles-bus.component';

describe('VehiclesBusComponent', () => {
  let component: VehiclesBusComponent;
  let fixture: ComponentFixture<VehiclesBusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VehiclesBusComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VehiclesBusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GestioVigilantsComponent } from './gestio-vigilants.component';

describe('GestioVigilantsComponent', () => {
  let component: GestioVigilantsComponent;
  let fixture: ComponentFixture<GestioVigilantsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GestioVigilantsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GestioVigilantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

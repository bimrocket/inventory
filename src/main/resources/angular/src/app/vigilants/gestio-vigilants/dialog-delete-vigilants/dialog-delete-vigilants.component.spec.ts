import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeleteVIgilantsComponent } from './dialog-delete-vigilants.component';

describe('DialogDeleteVIgilantsComponent', () => {
  let component: DialogDeleteVIgilantsComponent;
  let fixture: ComponentFixture<DialogDeleteVIgilantsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeleteVIgilantsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeleteVIgilantsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

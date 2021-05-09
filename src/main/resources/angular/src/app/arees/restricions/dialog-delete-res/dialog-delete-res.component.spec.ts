import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeleteModComponent } from './dialog-delete-mod.component';

describe('DialogDeleteModComponent', () => {
  let component: DialogDeleteModComponent;
  let fixture: ComponentFixture<DialogDeleteModComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeleteModComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeleteModComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

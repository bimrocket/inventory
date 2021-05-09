import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeletePPComponent } from './dialog-delete-pp.component';

describe('DialogDeletePPComponent', () => {
  let component: DialogDeletePPComponent;
  let fixture: ComponentFixture<DialogDeletePPComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeletePPComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeletePPComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogMassEditTargetaComponent } from './dialog-mass-edit-targeta.component';

describe('DialogMassEditTargetaComponent', () => {
  let component: DialogMassEditTargetaComponent;
  let fixture: ComponentFixture<DialogMassEditTargetaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogMassEditTargetaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogMassEditTargetaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

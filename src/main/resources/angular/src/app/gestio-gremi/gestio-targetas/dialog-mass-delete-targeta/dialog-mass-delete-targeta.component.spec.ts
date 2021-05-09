import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogMassDeleteTargetaComponent } from './dialog-mass-delete-targeta.component';

describe('DialogMassDeleteTargetaComponent', () => {
  let component: DialogMassDeleteTargetaComponent;
  let fixture: ComponentFixture<DialogMassDeleteTargetaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogMassDeleteTargetaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogMassDeleteTargetaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogDeleteTargetaComponent } from './dialog-delete-targeta.component';

describe('DialogDeleteTargetaComponent', () => {
  let component: DialogDeleteTargetaComponent;
  let fixture: ComponentFixture<DialogDeleteTargetaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogDeleteTargetaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogDeleteTargetaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

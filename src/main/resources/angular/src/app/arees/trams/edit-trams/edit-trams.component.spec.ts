import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditTramsComponent } from './edit-trams.component';

describe('EditTramsComponent', () => {
  let component: EditTramsComponent;
  let fixture: ComponentFixture<EditTramsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditTramsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditTramsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

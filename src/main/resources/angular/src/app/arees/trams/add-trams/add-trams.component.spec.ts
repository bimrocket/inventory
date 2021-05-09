import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTramsComponent } from './add-trams.component';

describe('AddTramsComponent', () => {
  let component: AddTramsComponent;
  let fixture: ComponentFixture<AddTramsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddTramsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddTramsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCondicionsComponent } from './edit-condicions.component';

describe('EditCondicionsComponent', () => {
  let component: EditCondicionsComponent;
  let fixture: ComponentFixture<EditCondicionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditCondicionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditCondicionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RestricionsComponent } from './restricions.component';

describe('RestricionsComponent', () => {
  let component: RestricionsComponent;
  let fixture: ComponentFixture<RestricionsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RestricionsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RestricionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

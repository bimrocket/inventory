import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PolitiquesPComponent } from './politiques-p.component';

describe('PolitiquesPComponent', () => {
  let component: PolitiquesPComponent;
  let fixture: ComponentFixture<PolitiquesPComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PolitiquesPComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PolitiquesPComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

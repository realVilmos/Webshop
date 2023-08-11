import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VendorCreatorComponent } from './vendor-creator.component';

describe('VendorCreatorComponent', () => {
  let component: VendorCreatorComponent;
  let fixture: ComponentFixture<VendorCreatorComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VendorCreatorComponent]
    });
    fixture = TestBed.createComponent(VendorCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

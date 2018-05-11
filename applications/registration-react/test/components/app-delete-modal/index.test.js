import React from 'react';
import { shallow } from 'enzyme';
import AppDeleteModal from '../../../src/components/app-delete-modal';

let defaultProps, wrapper, toggle, handleAction;

beforeEach(() => {
  toggle = jest.fn();
  handleAction = jest.fn();
  defaultProps = {
    toggle,
    handleAction
  };
  wrapper = shallow(<AppDeleteModal {...defaultProps} />);
});

test('should render AppDeleteModal correctly', () => {
  expect(wrapper).toMatchSnapshot();
});

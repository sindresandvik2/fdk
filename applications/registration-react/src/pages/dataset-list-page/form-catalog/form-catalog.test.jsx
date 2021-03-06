import React from 'react';
import { shallow } from 'enzyme';
import { FormCatalog } from './form-catalog.component';
import helptext from '../../../../test/fixtures/helptext';

let defaultProps;
let wrapper;

beforeEach(() => {
  const { helptextItems } = helptext;
  defaultProps = {
    helptextItems,
    initialValues: {
      title: 'Title',
      publisher: 'Publisher'
    },
    values: {
      description: {
        nb: 'Beskrivelse'
      }
    }
  };
  wrapper = shallow(<FormCatalog {...defaultProps} />);
});

test('should render FormCatalog correctly', () => {
  expect(wrapper).toMatchSnapshot();
});

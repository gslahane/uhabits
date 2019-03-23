/*
 * Copyright (C) 2016-2019 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

import PropTypes from 'prop-types';
import React from 'react';
import { StyleSheet, View } from 'react-native';
import { Colors } from '../../helpers/Colors';
import HabitListHeader from './HabitListHeader';
import HabitList from './HabitList';

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.appBackground,
  },
});

export default class ListHabitsScene extends React.Component {
  render() {
    const { onClickHabit, onClickCheckmark } = this.props;
    return (
      <View style={styles.container}>
        <HabitListHeader />
        <HabitList
          onClickHabit={onClickHabit}
          onClickCheckmark={onClickCheckmark}
        />
      </View>
    );
  }
}

ListHabitsScene.propTypes = {
  onClickHabit: PropTypes.func.isRequired,
  onClickCheckmark: PropTypes.func.isRequired,
};

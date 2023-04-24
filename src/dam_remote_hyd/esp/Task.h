#ifndef __TASK__
#define __TASK__

class Task {

private:
    int myPeriod;
    int timeElapsed;
    bool active;
    bool done;

public:
    virtual void init(int period) {
        this->myPeriod = period;
        this->timeElapsed = 0;
        this->active = false;
        this->done = false;
    }

    virtual void tick() = 0;

    virtual bool updateAndCheckTime(int basePeriod) {
        this->timeElapsed += basePeriod;

        if (this->timeElapsed >= this->myPeriod) {
            this->timeElapsed = 0;
            return true;
        } else {
            return false;
        }
    }

    virtual bool isActive() {
        return active;
    }

    /* This methods sets the task active or deactive */
    virtual void setActive(bool newActive) {
        this->timeElapsed = 0;
        this->active = newActive;
        if (newActive) {
            this->done = false;
        }
    }

    virtual bool isDone() {
        return this->done;
    }

    /* This methods set the task on the state Done */
    virtual void setDone() {
        this->done = true;
    }

};

#endif

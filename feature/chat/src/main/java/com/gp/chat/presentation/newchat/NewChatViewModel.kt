import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gp.socialapp.database.model.UserEntity
import com.gp.users.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewChatViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    init {
        getAllUsers()
    }

    private val _users = MutableStateFlow<List<UserEntity>>(emptyList())
    val users: StateFlow<List<UserEntity>>
        get() = _users
    fun getAllUsers(){
        viewModelScope.launch (Dispatchers.IO) {
            userRepository.fetchAllUsers().collect{
                _users.value=it

            }
        }
    }
}
